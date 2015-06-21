'use strict';

angular.module('petzioApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('clinicDetail.dashboard', {
                url: '/dashboard',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'petzioApp.clinic.home.title'
                },
                views: {
                    'content@entity': {
                        templateUrl: 'scripts/app/entities/clinic/clinic.dashboard/clinic.dashboard.html',
                        controller: 'ClinicDashboardController'
                    }
                }
            });
    });
