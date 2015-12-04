'use strict';

angular.module('coworkApp').controller('FavDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Fav', 'Space', 'User',
        function($scope, $stateParams, $modalInstance, entity, Fav, Space, User) {

        $scope.fav = entity;
        $scope.spaces = Space.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Fav.get({id : id}, function(result) {
                $scope.fav = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('coworkApp:favUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.fav.id != null) {
                Fav.update($scope.fav, onSaveFinished);
            } else {
                Fav.save($scope.fav, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
