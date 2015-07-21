'use strict';

angular.module('petzioApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('clinic', {
                parent: 'entity',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'petzioApp.clinic.home.title'
                },
                resolve: {
                    // translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    //     $translatePartialLoader.addPart('clinic');
                    //     return $translate.refresh();
                    // }]
                }
            })
            .state('clinicList', {
                parent: 'clinic',
                url: '/clinics',
                views: {
                    'content@entity': {
                        templateUrl: 'scripts/app/entities/clinic/clinics.html',
                        controller: 'ClinicController'
                    }
                }
            })
            .state('clinicDetail', {
                parent: 'clinic',
                url: '/clinics/:id',
                views: {
                    'content@entity': {
                        templateUrl: 'scripts/app/entities/clinic/clinic-detail.html',
                        controller: 'ClinicDetailController'
                    }
                }
            })
            .state('clinicForm', {
                parent: 'clinic',
                url: '/clinics/:id/form',
                views: {
                    'content@entity': {
                        templateUrl: 'scripts/app/entities/clinic/clinic-form.html',
                        controller: 'ClinicFormController'
                    }
                }
            });
    });
