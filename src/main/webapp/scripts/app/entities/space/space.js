'use strict';

angular.module('coworkApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('space', {
                parent: 'entity',
                url: '/spaces',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Spaces'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/spaces.html',
                        controller: 'SpaceController'
                    }
                },
                resolve: {
                }
            })
            .state('space.detail', {
                parent: 'entity',
                url: '/space/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Space'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/space/space-detail.html',
                        controller: 'SpaceDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Space', function($stateParams, Space) {
                        return Space.get({id : $stateParams.id});
                    }]
                }
            })
            .state('space.new', {
                parent: 'space',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/space/space-dialog.html',
                        controller: 'SpaceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    title: null,
                                    description: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('space');
                    })
                }]
            })
            .state('space.edit', {
                parent: 'space',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/space/space-dialog.html',
                        controller: 'SpaceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Space', function(Space) {
                                return Space.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('space', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
