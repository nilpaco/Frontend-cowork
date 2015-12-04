'use strict';

angular.module('coworkApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('fav', {
                parent: 'entity',
                url: '/favs',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Favs'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/fav/favs.html',
                        controller: 'FavController'
                    }
                },
                resolve: {
                }
            })
            .state('fav.detail', {
                parent: 'entity',
                url: '/fav/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Fav'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/fav/fav-detail.html',
                        controller: 'FavDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Fav', function($stateParams, Fav) {
                        return Fav.get({id : $stateParams.id});
                    }]
                }
            })
            .state('fav.new', {
                parent: 'fav',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/fav/fav-dialog.html',
                        controller: 'FavDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('fav', null, { reload: true });
                    }, function() {
                        $state.go('fav');
                    })
                }]
            })
            .state('fav.edit', {
                parent: 'fav',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/fav/fav-dialog.html',
                        controller: 'FavDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Fav', function(Fav) {
                                return Fav.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('fav', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
