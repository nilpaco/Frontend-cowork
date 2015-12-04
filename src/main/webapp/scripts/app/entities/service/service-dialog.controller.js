'use strict';

angular.module('coworkApp').controller('ServiceDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Service', 'Space',
        function($scope, $stateParams, $modalInstance, entity, Service, Space) {

        $scope.service = entity;
        $scope.spaces = Space.query();
        $scope.load = function(id) {
            Service.get({id : id}, function(result) {
                $scope.service = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('coworkApp:serviceUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.service.id != null) {
                Service.update($scope.service, onSaveFinished);
            } else {
                Service.save($scope.service, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
