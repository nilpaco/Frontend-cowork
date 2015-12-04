'use strict';

angular.module('coworkApp')
    .controller('ServiceController', function ($scope, Service, ParseLinks) {
        $scope.services = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Service.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.services.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.services = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Service.get({id: id}, function(result) {
                $scope.service = result;
                $('#deleteServiceConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Service.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteServiceConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.service = {
                name: null,
                id: null
            };
        };
    });
