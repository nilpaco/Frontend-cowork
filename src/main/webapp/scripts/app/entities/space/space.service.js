factory('Spaces', function($resource, DteUtils){
    return $resource('api/space/:id',{},{
       'query':{method: 'GET', isArray: true},
        'thisWeek':{method: 'GET', isArray: false, url: 'api/spaces'}
    });
});
