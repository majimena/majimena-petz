'use strict';

angular.module('petzioApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('entity', {
                abstract: true,
                parent: 'main',
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/entity.html'
                    }
                },
            });
    });
