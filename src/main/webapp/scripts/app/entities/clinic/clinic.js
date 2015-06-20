'use strict';

angular.module('petzioApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('clinic', {
                parent: 'entity',
                url: '/clinics',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'petzioApp.clinic.home.title'
                },
                views: {
                    'content@main': {
                        templateUrl: 'scripts/app/entities/clinic/clinics.html',
                        controller: 'ClinicController'
                    }
                },
                resolve: {
                    // translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    //     $translatePartialLoader.addPart('clinic');
                    //     return $translate.refresh();
                    // }]
                }
            })
            .state('clinicDetail', {
                parent: 'entity',
                url: '/clinics/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'petzioApp.clinic.detail.title'
                },
                views: {
                    'content@main': {
                        templateUrl: 'scripts/app/entities/clinic/clinic-detail.html',
                        controller: 'ClinicDetailController'
                    }
                },
                resolve: {
                    // translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    //     $translatePartialLoader.addPart('clinic');
                    //     return $translate.refresh();
                    // }]
                }
            })
            .state('clinicForm', {
                parent: 'entity',
                url: '/clinics/:id/form',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'petzioApp.clinic.form.title'
                },
                views: {
                    'content@main': {
                        templateUrl: 'scripts/app/entities/clinic/clinic-form.html',
                        controller: 'ClinicFormController'
                    }
                },
                resolve: {
                    // translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    //     $translatePartialLoader.addPart('clinic');
                    //     return $translate.refresh();
                    // }]
                }
            });
    });
