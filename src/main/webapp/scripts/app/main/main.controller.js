'use strict';

angular.module('coworkApp')
    .controller('MainController', function ($scope, Principal, Space, ParseLinks) {
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
        })
        /*.controller('SearcjController', function ($scope, SearchService) {
            $scope.search = function () {
                console.log("Search term is: " + $scope.term);
                SearchService.query($scope.term).then(function(response){
                    $scope.searchResult = response.data;
                });
            }
        })*/
    });
