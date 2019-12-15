import connexion
import json
import shelve
import uuid
import warnings
from flask import make_response, jsonify, request

from dao import citizens, incidents
from props.application_properties import ApplicationProperties


def warn(*args, **kwargs):
    pass


warnings.warn = warn
app = connexion.App(__name__, specification_dir='swagger/')

cors_headers = {
    'Content-Type': 'application/json; charset=utf-8',
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
    "Access-Control-Allow-Headers": "*"
}


class System(object):

    @staticmethod
    def index():
        return 'brain-{}'.format(ApplicationProperties.VERSION.value)


class Citizens(object):

    @staticmethod
    def get(uid):
        return make_response(
            jsonify(citizens.get(uid)),
            200,
            cors_headers
        )

    @staticmethod
    def get_eco_points(uid):
        return make_response(
            jsonify(citizens.get(uid)['ecoPoints']),
            200,
            cors_headers
        )

    @staticmethod
    def get_incidents(uid):
        incident_uids = citizens.get(uid)['incidents']
        data = []
        for incident_uid in incident_uids:
            data.append(incidents.get(incident_uid))
        return make_response(
            jsonify(data),
            200,
            cors_headers
        )

    @staticmethod
    def declare_incident(uid):
        incident_uid = incidents.create(uid, json.loads(request.data.decode('utf-8')))
        return make_response(
            jsonify({
                'incidentUid': incident_uid
            }),
            200,
            cors_headers
        )


class Incidents(object):

    @staticmethod
    def get(uid):
        return make_response(
            jsonify(incidents.get(uid)),
            200,
            cors_headers
        )

    @staticmethod
    def decline(uid):
        incidents.decline(uid)
        return make_response(
            jsonify('done'),
            200,
            cors_headers
        )

    @staticmethod
    def approve(uid):
        incidents.approve(uid)
        return make_response(
            jsonify('done'),
            200,
            cors_headers
        )

    @staticmethod
    def get_pending_incidents():
        return make_response(
            jsonify(incidents.get_pending()),
            200,
            cors_headers
        )


def create_dummy_citizen():
    uid = str(uuid.uuid1())
    with shelve.open('data/citizens') as c:
        c[uid] = {
            'name': 'foo',
            'surname': 'bar',
            'socialSecurityNumber': 123456789,
            'ecoPoints': 10,
            'incidents': []
        }
    return uid


app.add_api('swagger.yml', options={"swagger_ui": False})

if __name__ == '__main__':
    port = ApplicationProperties.PORT.value
    print('app running on port {}'.format(port))
    app.run(host=ApplicationProperties.HOST_IP.value, port=port, server=ApplicationProperties.SERVER.value)
