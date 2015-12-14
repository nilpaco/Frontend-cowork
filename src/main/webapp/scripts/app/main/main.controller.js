'use strict';

angular.module('coworkApp')
    .controller('MainController', function ($scope, Principal, Space, Fav, ParseLinks) {
        $scope.spaces = [];
        $scope.page = 0;
        $scope.loadAll = function(){
            Space.query({page: $scope.page, size: 3}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.spaces.push(result[i]);
                }
            });
        };

        $scope.reset = function() {
            $scope.page = 0;
            $scope.spaces = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.space = {
                title: null,
                description: null,
                id: null
            };
        };

        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });

        $scope.addFav = function (id) {
            Space.addFav({id: id});
        };

        $scope.spaces2 = [];
        $scope.page2 = 0;
        $scope.spaceByUser = function(){
            Space.spaceByUser({page2: $scope.page2, size: 10}, function(result2, headers2) {
                $scope.links2 = ParseLinks.parse(headers2('link'));
                for (var i = 0; i < result2.length; i++) {
                    $scope.spaces2.push(result2[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page2 = 0;
            $scope.spaces2 = [];
            $scope.spaceByUser();
        };
        $scope.loadPage2 = function(page2) {
            $scope.page = page2;
            $scope.spaceByUser();
        };
        $scope.spaceByUser();

        $scope.delete = function (id) {
            Space.get({id: id}, function(result) {
                $scope.space = result;
                $('#deleteSpaceConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Space.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteSpaceConfirmation').modal('hide');
                    $scope.clear();
                });
        };


        $scope.favs = [];
        $scope.pageFav = 0;
        $scope.favByUser = function(){
            Fav.favByUser({pageFav: $scope.pageFav, size: 3}, function(result3, headers3) {
                $scope.links3 = ParseLinks.parse(headers3('link'));
                for (var i = 0; i < result3.length; i++) {
                    $scope.favs.push(result3[i]);
                }
            });
        };

        $scope.favByUser();

        Principal.identity(true).then(function(account) {
            $scope.settingsAccount = account;
        });

        $scope.deletes = function (id) {
            Fav.get({id: id}, function(result) {
                $scope.fav = result;
                $('#deleteFavConfirmation').modal('show');
            });
        };

        $scope.confirmDeletes = function (id) {
            Fav.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteFavConfirmation').modal('hide');
                    $scope.clear();
                });
        };
    });

