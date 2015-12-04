'use strict';

angular.module('coworkApp')
    .controller('FavDetailController', function ($scope, $rootScope, $stateParams, entity, Fav, Space, User) {
        $scope.fav = entity;
        $scope.load = function (id) {
            Fav.get({id: id}, function(result) {
                $scope.fav = result;
            });
        };
        var unsubscribe = $rootScope.$on('coworkApp:favUpdate', function(event, result) {
            $scope.fav = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
