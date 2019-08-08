import {openDB} from 'idb';

const dbName = 'IQB';
const dbVersion = 3;
const storeName = 'iqd-rolls';

export async function loadRolls() {
  if (!('indexedDB' in window)) {
    console.log('This browser doesn\'t support IndexedDB');
    return;
  }

  let loadData = false;
  const db = await openDB(dbName, dbVersion, {
    async upgrade(db, oldVersion, newVersion, transaction) {
      if (!db.objectStoreNames.contains(storeName)) {
        await db.createObjectStore(storeName);
      }
    },
  });
  console.log(db.objectStoreNames);

  let count = await db.count(storeName);
  console.log("currently " + count + " entries in indexedDb");
  loadData = count != 18900; //number of rolls we should have

  if(loadData) {
    console.log('loading rolls');
    let request = new XMLHttpRequest();
    request.responseType = 'json';

    request.addEventListener('progress', function (e) {
      let percent_complete = (e.loaded / e.total) * 100;
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
        let tx = db.transaction(storeName, 'readwrite');
        let store = tx.store;
        store.clear();
        for (let key of Object.keys(request.response)) {
          store.put({
            ...request.response[key],
          }, key);
        }
        await tx.done;
        console.log("data inserted in indexedDB")
        console.timeEnd('inserting')
      }
    });

    request.open('get', 'rolls/rolls.json');
    request.send();
  } else {
    console.log("data loading not needed")
  }
  return;
};

export async function getRollResult(key: string): Promise<{[key: string]: number}> {
  //const results = await db.get(storeName, key);
  const db = await openDB(dbName, dbVersion);
  return db.get(storeName, key);
}

// Get a value from a store:
//const value = await db.get(storeName, key);