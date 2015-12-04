'use strict';

angular.module('coworkApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


