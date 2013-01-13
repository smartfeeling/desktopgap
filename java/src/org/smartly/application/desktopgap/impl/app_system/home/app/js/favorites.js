!(function (window) {

    var desktopgap = window.desktopgap
        , registry = desktopgap['registry']
        ;

    function Favorites() {
        this['favorites'] = _.bind(_init, this)();
    }

    Favorites.prototype.has = function (item) {
        return _.bind(_containsFavorite, this)(item);
    };

    Favorites.prototype.add = function (item) {
        _.bind(_addFavorite, this)(item);
    };

    Favorites.prototype.remove = function (item) {
        _.bind(_removeFavorite, this)(item);
    };

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    function _init() {
        var favorites = this['favorites'];
        try {
            if (!favorites) {
                favorites = registry.get('favorites');
                // console.log('LOADED: ' + JSON.stringify(favorites));
                if (!favorites) {
                    favorites = registry.put('favorites', {});
                }
            }
        } catch (err) {
            console.error('(favorites.js) _init(): ' + err);
        }
        return favorites;
    }

    function _addFavorite(item) {
        // console.log('ADDING TO FAVORITES: ' + JSON.stringify(item));
        var favorites = this['favorites'];
        try {
            if (!!favorites && !!item['_id']) {
                item['is_favorite'] = true;
                favorites[item['_id']] = item;
                registry.put('favorites', favorites);
            }
        } catch (err) {
            console.error('(favorites.js) _addFavorite(): ' + err);
        }
    }

    function _removeFavorite(item) {
        var favorites = this['favorites'];
        try {
            if (!!favorites && !!item['_id']) {
                item['is_favorite'] = false;
                delete favorites[item['_id']];
                registry.put('favorites', favorites);
                // console.log('REMOVING: ' + JSON.stringify(item));
            }
        } catch (err) {
            console.error('(favorites.js)  _removeFavorite(): ' + err);
        }
    }

    function _containsFavorite(item) {
        var favorites = this['favorites']
            , result = false
            , id = _.isObject(item) ? item['_id'] : item
            ;
        try {
            if (!!favorites && !!item) {
                result = favorites.hasOwnProperty(id);
            }
        } catch (err) {
            console.error('(favorites.js)  _containsFavorite(): ' + err);
        }
        //console.log('FAVORITE ' + id + ': ' + result);
        return result;
    }

    // --------------------------------------------------------------------
    //               Exports
    // --------------------------------------------------------------------

    window.favorites = new Favorites();

})(this);