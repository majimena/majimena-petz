'use strict';

angular.module('majimenatestApp')
    .controller('SidebarContentController', ['$rootScope', '$scope', function ($rootScope, $scope) {
        $scope.isToggleSideMenu = function() {
            var toggle = $rootScope.getSetting('minifySideMenu');
            return toggle;
        };
    }])
;
