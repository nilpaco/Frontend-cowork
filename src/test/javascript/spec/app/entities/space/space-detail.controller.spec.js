'use strict';

describe('Space Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockSpace, MockService, MockFav, MockImage, MockReview, MockUser;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockSpace = jasmine.createSpy('MockSpace');
        MockService = jasmine.createSpy('MockService');
        MockFav = jasmine.createSpy('MockFav');
        MockImage = jasmine.createSpy('MockImage');
        MockReview = jasmine.createSpy('MockReview');
        MockUser = jasmine.createSpy('MockUser');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'Space': MockSpace,
            'Service': MockService,
            'Fav': MockFav,
            'Image': MockImage,
            'Review': MockReview,
            'User': MockUser
        };
        createController = function() {
            $injector.get('$controller')("SpaceDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'coworkApp:spaceUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
