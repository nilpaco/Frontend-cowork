 'use strict';

angular.module('coworkApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-coworkApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-coworkApp-params')});
                }
                return response;
            }
        };
    });
