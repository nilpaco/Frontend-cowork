'use strict';

angular.module('coworkApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'entity',
                url: '/',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Home'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/main/main.html',
                        controller: 'MainController'
                    }
                },
                resolve: {
                }
            })
    })
    .config(function ($stateProvider) {
        $stateProvider
            .state('profile', {
                parent: 'entity',
                url: '/profile',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Profile'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/profile/profile.html',
                        controller: 'MainController'
                    }
                },
                resolve: {
                }
            })
    });
