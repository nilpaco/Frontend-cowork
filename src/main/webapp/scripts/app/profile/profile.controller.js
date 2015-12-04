'use strict';

angular.module('coworkApp')
    .controller('ProfileController', function ($scope, Space, ParseLinks) {
        $scope.spaces = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Space.query({page: $scope.page, size: 20}, function(result, headers) {
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
    });
