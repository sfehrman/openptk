/* 
 * Accordion Class - requires Prototype 1.6+ and Script.aculo.us Effects 1.8+
 */
var Accordion = Class.create({
	initialize: function(options) {
		this.accordion = $(options.id);
		if (!this.accordion) {
			throw ("Attempted to initalize accordion with undefined element: " + id);
		}

		this.toggleClass = "accordion-toggle";
		this.activeClass = "active";
		this.contentClass = "accordion-content";
		this.type = options.type || 'vertical';

		this.contents = this.accordion.select('.' + this.contentClass);
		this.current = this.accordion.select('.' + this.activeClass)[1];
		this.isAnimating = false;
		this.toExpand = null;

		if(this.type == 'vertical') {
			this.maxHeight = this.contents.max(function(e) {
				return e.getHeight();
			});
		}

		this.initialHide();
		
		if(this.current) {
			this.current.previous('.' + this.toggleClass).addClassName(this.activeClass);
			
			if (this.type == 'vertical' && this.current.getHeight() != this.maxHeight) { 
				this.current.setStyle({height: this.maxHeight + "px"});
			}
		}

		options.event = options.event || 'click';
		this.accordion.select("." + this.toggleClass).invoke("observe", options.event, this.toggle.bindAsEventListener(this));
	},

	toggle: function(e) {
		var el = e.element();

		if (!this.isAnimating) {
			this.toExpand = el.next('.' + this.contentClass);
			this.animate();
			e.stop();
			return false;
		}
	},

	toggleReset: function(el) {

		if (!this.isAnimating) {
			this.toExpand = el.next('.' + this.contentClass);
			this.animate();

			return false;
		}
	},

	initialHide: function() {
		this.contents.each(function(e) {
			e.setStyle({'display': 'block'});

			if (e == this.current) {
				return;
			}

			if(this.type == 'horizontal') {
				e.setStyle({width: 0});
			}
			else {
				e.setStyle({height: 0});
			}
		}.bind(this));
	},

	animate: function() {
		var effects = [];
		var on_options = {
			sync: true,
			scaleFrom: 0,
			scaleContent: false,
			scaleMode: 'contents',
			scaleX: false,
			scaleY: true
		};
		var off_options = {
			sync: true,
			scaleContent: false,
			scaleX: false,
			scaleY: true
		};

		if(this.type == 'vertical-multiple') {
			if(this.toExpand.previous('.' + this.toggleClass).hasClassName(this.activeClass)) {
				effects.push(new Effect.Scale(this.toExpand, 0, off_options));
				this.toExpand.previous('.' + this.toggleClass).removeClassName(this.activeClass);
				this.toExpand.removeClassName(this.activeClass);
			}
			else {
				effects.push(new Effect.Scale(this.toExpand, 100, on_options));
				this.toExpand.previous('.' + this.toggleClass).addClassName(this.activeClass);
				this.toExpand.addClassName(this.activeClass);
			}
		}
		else {
			if (this.toExpand == this.current) {
				return;
			}

			if(this.type == 'horizontal') {
				on_options.scaleX = off_options.scaleX = true;
				on_options.scaleY = off_options.scaleY = false;
			}
			else {
				on_options.scaleMode = {originalHeight: this.maxHeight, originalWidth: this.accordion.getWidth()};
			}

			effects.push(new Effect.Scale(this.toExpand, 100, on_options));
			effects.push(new Effect.Scale(this.current, 0, off_options));
			
			this.current.previous('.' + this.toggleClass).removeClassName(this.activeClass);
			this.current.removeClassName(this.activeClass);
			this.toExpand.previous('.' + this.toggleClass).addClassName(this.activeClass);
			this.toExpand.addClassName(this.activeClass);
		}

		new Effect.Parallel(effects, {
			duration: 0.75,
			fps: 25,
			queue: {position: 'end', scope: this.accordion.id + "Animation"},
			beforeStart: function() {
				this.isAnimating = true;
			}.bind(this),
			afterFinish: function() {
				this.current = this.toExpand;
				this.isAnimating = false;
			}.bind(this)
		});
	}
});