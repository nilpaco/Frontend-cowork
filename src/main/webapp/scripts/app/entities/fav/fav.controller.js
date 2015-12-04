'use strict';

angular.module('coworkApp')
    .controller('FavController', function ($scope, Fav, ParseLinks) {
        $scope.favs = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Fav.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.favs.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.favs = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Fav.get({id: id}, function(result) {
                $scope.fav = result;
                $('#deleteFavConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Fav.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteFavConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.fav = {
                id: null
            };
        };
    });
