'use strict';

angular.module('coworkApp')
    .controller('ReviewController', function ($scope, Review, ParseLinks) {
        $scope.reviews = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Review.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.reviews.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.reviews = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Review.get({id: id}, function(result) {
                $scope.review = result;
                $('#deleteReviewConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Review.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteReviewConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.review = {
                description: null,
                date: null,
                id: null
            };
        };
    });
