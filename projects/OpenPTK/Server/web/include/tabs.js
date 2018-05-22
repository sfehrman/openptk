/* 
 * Tab Class - requires Prototype 1.6+ and Script.aculo.us Effects 1.8+
 */
var Tab = Class.create({
   initialize: function(options) {
      this.tab = $(options.id);
      if (!this.tab) {
         throw ("Attempted to initalize tab with undefined element: " + options.id);
      }
      var styles = {
         'display': 'none',
         'opacity': 0
      };

      this.toggleClass = "tabset_tabs";
      this.activeClass = "active";
      this.contentClass = "tabset_content";
      this.current = 0;
      this.tabs = this.tab.select("." + this.toggleClass + " li");
      this.contents = this.tab.select('.' + this.contentClass);
      this.isAnimating = false;


      // setup event listeners and determine current
      options.event = options.event || 'click';
      hash = 0;
      this.tabs.each(function(e, i) {
         if(e.hasClassName(this.activeClass)) {
            this.current = i;
         }
         if(window.location.hash == ('#' + e.down('a').href.split('#')[1])) {
            hash = i;
         }
         e.observe(options.event, this.toggle.bindAsEventListener(this, i));
      }.bind(this));

      if(hash) {
         this.tabs[this.current].removeClassName(this.activeClass);
         this.current = hash;
      }
      this.tabs[this.current].addClassName(this.activeClass);

      // set as equal heights if options is set
      if(options.height == true) {
         styles['height'] = this.contents.max(function(e) {
            return e.getHeight();
         }) + "px";
			
         this.contents[this.current].setStyle({
            'height': styles.height
         });
      }

      if(options.rounded == true) {
         var elem = this.tab.select('.' + this.contentClass + '_container')[0];
         if(elem) {
            elem.setStyle({
               paddingTop: 0,
               paddingBottom: 0
            });
            elem.insert({
               top: '<div class="tr"></div>'
            });
            elem.insert('<div class="bl"><div class="br"></div></div>');
         }
      }

      // initialize display
      this.contents.each(function(section, i) {
         if(this.current != i) {
            section.setStyle(styles);
         }
      }.bind(this));
   },
   toggle: function(e, i) {
      if(this.isAnimating || this.current == i) {
         return false;
      }

      //// Hide upload photo field if not on tab 0, check presense first
      if ($('upphotodiv')!=null) {
         if(i!=0)
         {
            $('upphotodiv').hide();
         }
         if(i==0)
         {
            $('upphotodiv').show();
         }
      }

      e.stop();

      // hide old, reset new, toggle class names
      this.tabs[i].addClassName(this.activeClass);
      this.contents[this.current].setStyle({
         'display': 'none',
         'opacity': 0
      });
      this.tabs[this.current].removeClassName(this.activeClass);
      this.contents[i].setStyle({
         'display': 'block'
      });

      // fade in new with effects library
      new Effect.Opacity(this.contents[i], {
         from: 0,
         to: 1,
         transition: Effect.Transitions.sinoidal,
         duration: 0.3,
         beforeStart: function() {
            this.isAnimating = true;
         }.bind(this),
         afterFinish: function() {
            this.isAnimating = false;
            this.current = i;
         }.bind(this)
      });

   // set browser hash for those that support it
   //window.location.hash = this.tabs[i].down('a').href.split('#')[1];
   },
   toggleTab: function(i) {
      if(this.isAnimating || this.current == i) {
         return false;
      }

      if ($('upphotodiv')!=null) {
         if(i!=0)
         {
            $('upphotodiv').hide();
         }
         if(i==0)
         {
            $('upphotodiv').show();
         }
      }
      //e.stop();

      // hide old, reset new, toggle class names
      this.tabs[i].addClassName(this.activeClass);
      this.contents[this.current].setStyle({
         'display': 'none',
         'opacity': 0
      });
      this.tabs[this.current].removeClassName(this.activeClass);
      this.contents[i].setStyle({
         'display': 'block'
      });

      // fade in new with effects library
      new Effect.Opacity(this.contents[i], {
         from: 0,
         to: 1,
         transition: Effect.Transitions.sinoidal,
         duration: 0.3,
         beforeStart: function() {
            this.isAnimating = true;
         }.bind(this),
         afterFinish: function() {
            this.isAnimating = false;
            this.current = i;
         }.bind(this)
      });

   // set browser hash for those that support it
   //window.location.hash = this.tabs[i].down('a').href.split('#')[1];
   }
});