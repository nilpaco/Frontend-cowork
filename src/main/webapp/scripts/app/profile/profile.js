'use strict';

angular.module('coworkApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('profile', {
                url: '/profile',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Profile'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/profile/profile.html',
                        controller: 'ProfileController'
                    }
                },
                resolve: {
                    spaces: function(Space) {
                        return Space.get().$promise;
                    },

                }
            })
    });

