'use strict';

angular.module('coworkApp')
    .controller('ServiceDetailController', function ($scope, $rootScope, $stateParams, entity, Service, Space) {
        $scope.service = entity;
        $scope.load = function (id) {
            Service.get({id: id}, function(result) {
                $scope.service = result;
            });
        };
        var unsubscribe = $rootScope.$on('coworkApp:serviceUpdate', function(event, result) {
            $scope.service = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
