'use strict';

angular.module('coworkApp').controller('ReviewDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Review', 'Space', 'User',
        function($scope, $stateParams, $modalInstance, entity, Review, Space, User) {

        $scope.review = entity;
        $scope.spaces = Space.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Review.get({id : id}, function(result) {
                $scope.review = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('coworkApp:reviewUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.review.id != null) {
                Review.update($scope.review, onSaveFinished);
            } else {
                Review.save($scope.review, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
