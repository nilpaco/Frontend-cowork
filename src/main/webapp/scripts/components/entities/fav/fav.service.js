'use strict';

angular.module('coworkApp')
    .factory('Fav', function ($resource, DateUtils) {
        return $resource('api/favs/:id', {}, {
            'query': { method: 'GET', isArray: true},
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
