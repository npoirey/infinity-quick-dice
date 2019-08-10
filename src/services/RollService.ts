import {openDB} from 'idb';

const dbName = 'IQB';
const dbVersion = 3;
const storeName = 'iqd-rolls';
const expectedRollsNumber = 18900;

export interface LoadingState {
  state: 'downloading' | 'extracting' | 'done',
  download?: number,
}

class Observable<T> {
  private callback: Function | null = null;

  subscribe(callback: Function | null) {
    this.callback = callback;
  }

  emit(value: T) {
    if (this.callback) {
      this.callback(value);
    }
  }
}

export const loadingStateObservable: Observable<LoadingState> = new Observable<LoadingState>();

export async function loadRolls() {
  if (!('indexedDB' in window)) {
    console.log('This browser doesn\'t support IndexedDB');
    return;
  }

  const db = await openDB(dbName, dbVersion, {
    async upgrade(db, oldVersion, newVersion, transaction) {
      if (!db.objectStoreNames.contains(storeName)) {
        await db.createObjectStore(storeName);
      }
    },
  });

  let count = await db.count(storeName);
  console.log('currently ' + count + ' entries in indexedDb');
  let loadData = count != expectedRollsNumber; //number of rolls we should have
  if (loadData) {
    console.log('loading rolls');
    loadingStateObservable.emit({
      state: 'downloading',
      download: 0,
    });
    let request = new XMLHttpRequest();
    request.responseType = 'json';

    request.addEventListener('progress', function (e) {
      let percent_complete = (e.loaded / e.total) * 100;
      loadingStateObservable.emit({
        state: 'downloading',
        download: percent_complete,
      });
      console.log(percent_complete);
    });

    request.addEventListener('readystatechange', async function (e) {
      if (request.readyState == 2 && request.status == 200) {
        console.log('starting download');
      } else if (request.readyState == 3) {
        console.log('Download is under progress');
      } else if (request.readyState == 4) {
        console.log('Downloading has finished');
        console.time('inserting');
        // request.response holds the file data
        loadingStateObservable.emit({
          state: 'extracting',
          download: 100,
        });

        let worker = new Worker('/PersistRollsWorker.js');
        worker.onmessage = (message) => {
          console.log('got response from worker', message);
          switch (message.data) {
            case 'ready':
              // posting to persist data in db now that the worker is ready
              worker.postMessage({
                action: 'load',
                storeName,
                rolls: request.response,
              });
              break;
            case 'loaded':
              console.timeEnd('inserting');
              loadingStateObservable.emit({
                state: 'done',
              });
              break;
          }
        };
        worker.postMessage({
          action: 'init',
          dbName,
          dbVersion,
        });
      }
    });

    request.open('get', 'rolls/rolls.json');
    request.send();
  } else {
    loadingStateObservable.emit({
      state: 'done',
    });
    console.log('data loading not needed');
  }
  return;
}

/**
 * get the roll results for the key (from indexeddb)
 * @param key
 */
export async function getRollResult(key: string): Promise<{ [key: string]: number }> {
  const db = await openDB(dbName, dbVersion);
  return db.get(storeName, key);
}
