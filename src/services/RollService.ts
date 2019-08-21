import {openDB} from 'idb';

const dbName = 'IQB';
const dbVersion = 3;
const storeName = 'iqd-rolls';
const expectedRollsNumber = 18900;
const estimatedUnzippedSize = 13701847; // size of rools.json unzipped

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
export const errorObservable: Observable<String> = new Observable<String>();

export async function loadRolls() {
  if (!('indexedDB' in window)) {
    throw new Error('This browser doesn\'t support IndexedDB');
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

    request.onerror = function (e) {
      errorObservable.emit('Request to load data failed');
    };

    request.addEventListener('progress', function (e) {
      let percent_complete;
      if (e.lengthComputable) {
        percent_complete = (e.loaded / e.total) * 100;
      } else {
        //gzip, estimate progress
        percent_complete = (e.loaded / estimatedUnzippedSize) * 100;
      }

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
        if (request.status != 200) {
          errorObservable.emit('Failed to download data, please reload');
          return;
        }
        console.log('Downloading has finished');
        console.time('inserting');
        loadingStateObservable.emit({
          state: 'extracting',
          download: 100,
        });

        let worker = new Worker('/PersistRollsWorker.js');
        worker.onerror = (error) => errorObservable.emit(error.message);
        worker.onmessage = (message) => {
          console.log('got response from worker', message && message.data);
          switch (message.data.state) {
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
            case 'error':
              errorObservable.emit(message.data.details);
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
