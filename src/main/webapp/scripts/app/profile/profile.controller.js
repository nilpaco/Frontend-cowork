'use strict';

angular.module('coworkApp')
    .controller('ProfileController', function ($scope, Principal, Space, Fav, ParseLinks) {
        $scope.spaces2 = [];
        $scope.page2 = 0;
        $scope.spaceByUser = function(){
            Space.spaceByUser({page2: $scope.page2, size: 3}, function(result2, headers2) {
                $scope.links2 = ParseLinks.parse(headers2('link'));
                for (var i = 0; i < result2.length; i++) {
                    $scope.spaces2.push(result2[i]);
                }
            });
        };
        $scope.spaceByUser();

    });
