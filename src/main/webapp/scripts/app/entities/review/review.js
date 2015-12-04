'use strict';

angular.module('coworkApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('review', {
                parent: 'entity',
                url: '/reviews',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Reviews'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/review/reviews.html',
                        controller: 'ReviewController'
                    }
                },
                resolve: {
                }
            })
            .state('review.detail', {
                parent: 'entity',
                url: '/review/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Review'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/review/review-detail.html',
                        controller: 'ReviewDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Review', function($stateParams, Review) {
                        return Review.get({id : $stateParams.id});
                    }]
                }
            })
            .state('review.new', {
                parent: 'review',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/review/review-dialog.html',
                        controller: 'ReviewDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    description: null,
                                    date: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('review', null, { reload: true });
                    }, function() {
                        $state.go('review');
                    })
                }]
            })
            .state('review.edit', {
                parent: 'review',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/review/review-dialog.html',
                        controller: 'ReviewDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Review', function(Review) {
                                return Review.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('review', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
