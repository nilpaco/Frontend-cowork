'use strict';

describe('Fav Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockFav, MockSpace, MockUser;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockFav = jasmine.createSpy('MockFav');
        MockSpace = jasmine.createSpy('MockSpace');
        MockUser = jasmine.createSpy('MockUser');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'Fav': MockFav,
            'Space': MockSpace,
            'User': MockUser
        };
        createController = function() {
            $injector.get('$controller')("FavDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'coworkApp:favUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
