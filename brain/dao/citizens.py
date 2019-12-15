import random
import shelve


def get(uid):
    with shelve.open('data/citizens') as c:
        citizen = c[uid]
        citizen['uid'] = uid
    return citizen


def add_incident_uid(citizenUid, incidentUid):
    with shelve.open('data/citizens') as c:
        citizen = c[citizenUid]
        incidents = citizen['incidents']
        incidents.append(incidentUid)
        c[citizenUid] = citizen


def increment_eco_points(uid):
    points = random.choice(range(10, 31))
    with shelve.open('data/citizens') as c:
        citizen = c[uid]
        citizen['ecoPoints'] = citizen['ecoPoints'] + points
        c[uid] = citizen
    return points
