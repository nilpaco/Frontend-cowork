'use strict';

angular.module('coworkApp')
    .controller('ProfileController', function ($scope, space) {
        $scope.spaceList = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Points.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.pointsList.push(result[i]);
                }
            });
        };

    });
