'use strict';

angular.module('coworkApp')
    .factory('Space', function ($resource, DateUtils) {
        return $resource('api/spaces/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'addFav': { method: 'POST', url: 'api/favspace/:id'},
            'spaceByUser': { method: 'GET', isArray: true, url: 'api/spaceByUser'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });

    });
