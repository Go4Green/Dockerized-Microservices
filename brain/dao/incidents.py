import shelve
import uuid

from dao.citizens import add_incident_uid, increment_eco_points
from dao.citizens import get as get_citizen


def get(uid):
    with shelve.open('data/incidents') as inc:
        incident = inc[uid]
        incident['uid'] = uid
    return incident


def decline(uid):
    with shelve.open('data/incidents') as inc:
        incident = inc[uid]
        incident['status'] = 'declined'
        inc[uid] = incident


def approve(uid):
    with shelve.open('data/incidents') as inc:
        incident = inc[uid]
        points = increment_eco_points(incident['citizenUid'])
        incident['status'] = 'approved'
        incident['ecoPointsAwarded'] = points
        inc[uid] = incident


def get_pending():
    data = []
    with shelve.open('data/incidents') as inc:
        for item in inc:
            if inc[item]['status'] == 'pending':
                d = inc[item]
                social_security_number = get_citizen(d['citizenUid'])['socialSecurityNumber']
                d['socialSecurityNumber'] = social_security_number
                data.append(d)
    return data


def create(citizenUid, incident):
    uid = str(uuid.uuid1())
    add_incident_uid(citizenUid, uid)
    incident['status'] = 'pending'
    incident['citizenUid'] = citizenUid
    incident['uid'] = uid
    with shelve.open('data/incidents') as inc:
        inc[uid] = incident
    return uid
