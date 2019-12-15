import * as domify from '../node_modules/domify/index';

const domIds = {
  incidents: 'incidents-cell',
  map: 'map-cell'
};

const config = {
  pending: 'http://172.16.176.42:5000/incidents/pending',
  approve: 'http://172.16.176.42:5000/incidents/{uid}/approve',
  decline: 'http://172.16.176.42:5000/incidents/{uid}/decline',
};

const views = {
  incident:
    `<div class="mdl-cell--stretch" id="{incident-id}" style="box-shadow: 5px 5px 10px grey; margin-bottom: 15px;">
       <div class="mdl-grid" style="justify-content: center">
         <div class="mdl-cell--stretch">Κατηγορία: {incident-category}</div>
       </div>
       <div class="mdl-grid">
         <div class="mdl-cell--stretch">ΑΜΚΑ: {incident-ssn}</div>
         <div class="mdl-layout-spacer"></div>
         <div class="mdl-cell--stretch">{incident-time}</div>
       </div>
       <div class="mdl-grid" style="width: 220px; height: 180px;">
         <img class="mdl-cell--12-col rotate90" src="{incident-img}"/>
       </div>
       <div class="mdl-grid">
         <div class="mdl-cell--stretch">Περιγραφή: {incident-description}</div>
       </div>
       <div class="mdl-grid">
         <a class="mdl-button mdl-js-button mdl-js-ripple-effect" id="incidents-accept">Αποδοχή</a>
         <div class="mdl-layout-spacer"></div>
         <a class="mdl-button mdl-js-button mdl-js-ripple-effect" id="incidents-decline">Άκυρο</a>
       </div>
    </div>`,
};

const incidentsCache = {};
const removed = [];

let map;
let marker;
window.initMap = () => {
  const mapsEl = document.getElementById("map");
  map = new google.maps.Map(mapsEl, {
    center: {
      lat: 37.9680948,
      lng: 23.7021083,
    },
    zoom: 16
  });
};

function startPolling() {
  const xhr = new XMLHttpRequest();
  xhr.open("GET", config.pending);
  xhr.onreadystatechange = function() {
    if(xhr.readyState === 4) {
      if(xhr.status >= 200 && xhr.status < 400) {
        updateIncidents(JSON.parse(xhr.responseText));
      } else {
        console.error('oopsie')
      }
    }
  };
  xhr.send();
}

function updateIncidents(incidents) {
  const el = document.getElementById(domIds.incidents);
  while(el.children.length > 0)
    el.removeChild(el.children.item(0));

  incidents.forEach(incident => {
    incidentsCache[incident['uid']] = incident;
    if(removed.indexOf(incident['uid']) > -1)
      return;

    let imgBase64 = 'data:image/jpeg;base64,' + incident['snap'];
    const html = views.incident
      .replace('{incident-id}', incident['uid'])
      .replace('{incident-ssn}', incident['socialSecurityNumber'])
      .replace('{incident-category}', incident['category'])
      .replace('{incident-img}', imgBase64/*'../images/download.jpg'*/)
      .replace('{incident-description}', incident.hasOwnProperty('incident') ? incident['description'] : 'Στο DnD θα τους δείρω όλους με τα μαγικά' +
        ' μου σπέλια')
      .replace('{incident-time}', new Date(incident['timestamp']).toLocaleString());
    el.appendChild(domify(html));
    document.getElementById(incident['uid']).addEventListener('click', selectIncident.bind(undefined, incident['uid']));
    document.getElementById("incidents-accept").addEventListener("click", acceptIncident.bind(undefined, incident['uid']));
    document.getElementById("incidents-decline").addEventListener("click", declineIncident.bind(undefined, incident['uid']));
  });
}

function acceptIncident(uid) {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", config.approve.replace("{uid}", uid));
  xhr.onreadystatechange = function() {
    if(xhr.readyState === 4) {
      if(xhr.status >= 200 && xhr.status < 400) {
        const incidentEl = document.getElementById(uid);
        incidentEl.parentElement.removeChild(incidentEl);
      } else {
        console.error('Backend has failed us.');
      }
    }
  };
  xhr.send();
}

function declineIncident(uid) {
  const xhr = new XMLHttpRequest();
  xhr.open("POST", config.decline.replace("{uid}", uid));
  xhr.onreadystatechange = function() {
    if(xhr.readyState === 4) {
      if(xhr.status >= 200 && xhr.status < 400) {
        const incidentEl = document.getElementById(uid);
        incidentEl.parentElement.removeChild(incidentEl);
      } else {
        console.error('Backend has failed us.');
      }
    }
  };
  xhr.send();
}

function selectIncident(uid) {
  if(marker) {
    marker.setMap(null);
  }
  const incident = incidentsCache[uid];
  const lat = incident['lat'];
  const lng = incident['lng'];
  const uluru = {
    lat: lat, lng: lng
  };

  marker = new google.maps.Marker({position: uluru, map: map});
  map.panTo(marker.getPosition());
}

setInterval(startPolling, 2000);
// startPolling();
