(function () {

    var EVENT_CLICK = 'click'
        , sel_self = '#item-<%= cid %>'
        , sel_title = '#title-<%= cid %>'
        , sel_description = '#description-<%= cid %>'
        , sel_image = '#image-<%= cid %>'
        , IMG_SRC = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAC00lEQVR4Xu1aW2riUBjOGQbBok0E79fggy19qbqBZgnuoHEHzgpqVzB2B3YF4+wgXcHYh2LpgxjFS2lBoz7rfJYDDXLiJXGqZ/CDn1P633P+SyCS+Xwu8IxvAucg+Xz+D86swCfqJJfLcV1D3x32wDGBYwKz2ez/vAFCiCIcEBCntlUCT09PD8IB4eLigu8eQJzOEzg/PxdxqJTkRZk1Go1HLhI4Ozu7hGwNDJkylOfn50cubiCTyYi0iSTK0F5eXh64KSHIlE3BL1DZQw/Y3wOQUZcYI+GLgRgc3YDE7RRKp9MphkwWxEcPNJtNXZblZUYJdMdTE9dwFEwMOZVK3ei6fsvTFCosMcrJZFJqt9s/9pkASSQSTE6n0yFmQchd46gyRFug2oKg88/6gsZpb4zShO7j8XiL3oZiLidQlhBS28cYJdFolJlZr9cjVsZisVjK9ErR6na7urAh4O+aLkKJnmX4MtbrseMkkUiEyej3+0TYMeDrCoe2xKiDFPgz1ujO9/o6HQ6HRTrNWDtFA18ZDAaGnSn0VWOQlg1gkUQoFFJeX1+Ng0sgGAxewY+6RixLe6J4YAnQPbIZ1EAgUH97e7vbeA/4/X4m5/39fSdNDPu0cbdCFv4fl+zY3APO53fFhloVlNtoD/h8PmZmw+HQ8Q3ANn36tlBGDLcmW87GqCRJP3GUTLO7MhqN7h3XvjVK8FmFD31lD4iiyOQYhkEECsjc4GAFo4FUyOrLDOjQ2neEKmwXqT32Jj49PWUyxuMxEQDwL+kTt8IIpEL+t5kBvV/0DdYpZNjWYc9OCdEFtBoSqOb1esuTyeSjZvF3CnoFYTcog4qWJeTxeJic6XS64G1bBlXoFaFH+2VnkEGtrccoeNs2oXpycrLQKwi7haU94na75ytWe104DGggZdseKAmHA8XOF5qCwAGIy+U6fqU8JnD8SskxCIjv30rwXkJ/AXUFovgG6GR5AAAAAElFTkSuQmCC'

        ;

    function PagedListItem(options) {
        var self = this;
        ly.base(this, {
            template: '/assets/lib/bootstrap/widgets/pagedlist/pagedlistitem.vhtml',
            model: false,
            view: false
        });

        options = options || {};

        self['_name'] = null != options['name'] ? options['name'] : 'name';
        self['_description'] = null != options['description'] ? options['description'] : 'description';
        self['_image'] = null != options['image'] ? options['image'] : 'image';
        self['_data'] = null != options['data'] ? options['data'] : 'data';

        // add Events
        self.on('init', _init);
    }

    ly.inherits(PagedListItem, ly.Gui);


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;

        if (!!self['_data']) {
            self.bindTo(_loadData)(self['_data']);
        }
    }

    function _loadData(item) {
        var self = this
            , $title = $(self.template(sel_title))
            , $description = $(self.template(sel_description))
            , $image = $(self.template(sel_image))
            ;

        $title.html(ly.value(item, self['_name']));
        $description.html(ly.value(item, self['_description']));
        $image.attr('src', ly.value(item, self['_image'] || IMG_SRC));

        //-- handler --//
        $(self.template(sel_self)).on('click', function (e) {
            e.stopPropagation();
            self.trigger(EVENT_CLICK, item);
            return false;
        });
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.PagedListItem');
    ly.gui.widgets.PagedListItem = PagedListItem;

})();
