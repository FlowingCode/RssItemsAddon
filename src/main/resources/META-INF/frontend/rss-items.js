/*
`rss-items`

Example:
```html
<rss-items
  url="https://content.therapychat.com/rss.xml"
  max="4"
  auto
></rss-items>
```

It will retrieve the items from the url automatically.

### Styling

The following custom properties and mixins are available for styling:

Custom property | Description | Default
----------------|-------------|----------
`--rss-items` | Mixin applied to the component | `{}`
`--rss-items-article` | Mixin applied to the articles | `{}`
`--rss-items-article-mq-m-up` | Mixin applied to the articles on `min-width: 600px` | `{}`
`--rss-items-article-mq-l-up` | Mixin applied to the articles on `min-width: 900px` | `{}`
`--rss-items-thumbnail` | Mixin applied to the image thumbnails | `{}`
`--rss-items-thumbnail-hover` | Mixin applied to the image thumbnails when hover | `{}`
`--rss-items-thumbnail-container` | Mixin applied to the image thumbnails container | `{}`
`--rss-items-title` | Mixin applied to the title | `{}`
`--rss-items-excerpt` | Mixin applied to the excerpt | `{}`

*/
/*
  FIXME(polymer-modulizer): the above comments were extracted
  from HTML and may be out of place here. Review them and
  then delete this comment!
*/
import '@polymer/polymer/polymer-legacy.js';

import '@polymer/iron-ajax/iron-ajax.js';
import '@polymer/iron-image/iron-image.js';
import * as X2JS from "x2js/x2js.js";
import {PolymerElement} from '@polymer/polymer/polymer-element.js';
import {html} from '@polymer/polymer/lib/utils/html-tag.js';

class RssItems extends PolymerElement {
	static get is() { return 'rss-items'; }
	
	static get template() {
		return html`
  <style>
    :host {
      display: flex;
      flex-wrap: wrap;
      flex-direction: row;
      align-items: flex-start;
      justify-content: space-between;

      @apply --rss-items;
    }

    * {
      box-sizing: border-box;
    }

    h3,
    p {
      margin: 0;
    }

    a {
      color: var(--primary-color, inherit);
      text-decoration: none;
    }

    article {
      margin-bottom: 2em;

      @apply --rss-items-article;
    }

    .thumbnail-container {
      display: block;
      overflow: hidden;
      width: 100%;
      height: 180px;

      @apply --rss-items-thumbnail-container;
    }

    .thumbnail {
      width: 100%;
      height: 100%;
      transition: transform .5s ease-out;

      @apply --rss-items-thumbnail;
    }

    .thumbnail-container:hover .thumbnail,
    .thumbnail-container:focus .thumbnail {
      transform: scale3d(1.3, 1.3, 1);

      @apply --rss-items-thumbnail-hover;
    }

    .title {
      min-height: 3em;
      margin: 1em 0 .5em;

      @apply --rss-items-title;
    }

    .excerpt {
      min-height: 6em;
      margin: .5em 0 2em;

      @apply --rss-items-excerpt;
    }

    .read-more-icon {
      width: 1.2em;
      vertical-align: -.2em;
      margin-left: .8em;
    }

    @media (max-width: 599px) {
      .title,
      .excerpt {
        min-height: 0;
      }
    }

    @media (min-width: 600px) {
      article {
        flex: 1 1 40%;
        margin-right: 2em;

        @apply --rss-items-article-mq-m-up;
      }

      article:nth-of-type(2n),
      article:last-of-type {
        margin-right: 0;
      }
    }

    @media (min-width: 900px) {
      article {
        flex: 1 1 30%;

        @apply --rss-items-article-mq-l-up;
      }

      article:nth-of-type(2n) {
        margin-right: 2em;
      }

      article:nth-of-type(3n) {
        margin-right: 0;
      }
    }
  </style>

  <iron-ajax id="rssAjax" url="[[url]]" handle-as="xml" on-response="_onRssResponse" auto="[[auto]]"></iron-ajax>

  <template is="dom-repeat" items="[[items]]">
    <article>

      <a class="thumbnail-container" href="[[item.link]]" title="[[item.title]]">
        <iron-image class="thumbnail" src="[[item.imageSrc]]" alt="[[item.title]]" sizing="cover"></iron-image>
      </a>

      <a href="[[item.link]]" title="[[item.title]]">
        <h3 class="title">[[_truncateText(item.title, maxTitleLength)]]</h3>
      </a>

      <div class="excerpt">[[_truncateText(item.excerpt, maxExcerptLength)]]</div>

      <template is="dom-if" if="[[showReadMore]]">
        <a href="[[item.link]]" class="read-more" title="[[readMoreAnchorTitle]][[item.title]]">[[readMoreAnchorText]]
          <img class="read-more-icon" src="./images/arrow-forward.svg" alt="[[readMoreImageAlt]]">
        </a>
      </template>

    </article>
  </template>
`;}

	  static get properties() {
			return {
    /**
     * If true init rss request.
     */
    auto: {
      type: Boolean,
      value: false
    },
    /**
     * The retrieved items array.
     */
    items: {
      type: Array,
      notify: true
    },
    /**
     * Max number of items to show. If it is undefined shows all items.
     */
    max: {
      type: Number
    },
    /**
    * Max length for item excerpts. If the excerpt exceeds this length it will be trimed and will have an ellipsis appended.
    */
    maxExcerptLength: {
      type: Number,
      value: 100
    },
    /**
     * Max length for item titles. If the title exceeds this length it will be trimed and will have an ellipsis appended.
     */
    maxTitleLength: {
      type: Number,
      value: 50
    },
    /**
    * Read more anchor text.
    */
    readMoreAnchorText: {
      type: Boolean,
      value: 'Leer más'
    },
    /**
    * Read more anchor title.
    */
    readMoreAnchorTitle: {
      type: Boolean,
      value: 'Leer más sobre: '
    },
    /**
    * Read more image alternative text.
    */
    readMoreImageAlt: {
      type: Boolean,
      value: 'Icono de flecha'
    },
    /**
     * If true the items elements will display a read more link.
     */
    showReadMore: {
      type: Boolean,
      value: false
    },
    /**
     * The URL of the RSS.
     */
    url: {
      type: String
    }
  }}

  /**
   * Init ajax request to get rss.
   */
  initRequest() {
    this.$.rssAjax.generateRequest()
  }

  /**
   * Ajax request callback. Get RSS and parse its items in an array.
   * @param {Object} ev Iron ajax event.
   * @param {Object} detail Iron ajax detail.
   */
  _onRssResponse(ev, detail) {
    this.xmlToItems(detail.response.documentElement)
  }

  /**
   * Receives a xml and set this.items as json.
   * @param {Object} xml XML element.
   */
  xmlToItems(xml) {
	var x2 = new X2JS()
	var json = x2.xml2js(xml)
	var items = json.rss ? json.rss.channel.item : json.channel.item
	// truncate with this.max and parse items
	items = this.max === undefined ? items : items.splice(0, this.max)
	this.items = this._parseItems(items)
  }

  /**
   * Parse items by getting excerpt and image source.
   * @param {Array} items RSS items.
   */
  _parseItems(items) {
    return items.map(function (item) {
      item.excerpt = this._getItemExcerpt(item)
      item.imageSrc = this._getItemImageScr(item)
      return item
    }.bind(this))
  }

  /**
   * Get excerpt from item description.
   * @param {Object} item Item where find excerpt.
   */
  _getItemExcerpt(item) {
    var element = document.createElement('div')
    element.innerHTML = item.description
    return element.textContent.trim()
  }

  /**
   * Get image source from item description.
   * @param {Object} item Item where find image.
   */
  _getItemImageScr(item) {
    if (item.thumbnail && item.thumbnail._url) {
      return item.thumbnail._url
    } else {
      var descriptionSrc = this.__getImageFromAttribute(item, true);
      return descriptionSrc ? descriptionSrc : this.__getImageFromAttribute(item, false);
    }
  }

  /**
   * 
   * @param {Object} item Item where find image.
   * @param {boolean} byDescription If true, search for image in item description, otherwise in encoded attribute.
   * @returns {String} Image src.
   */
  __getImageFromAttribute(item, byDescription) {
    var element = document.createElement('div')
    element.innerHTML = byDescription ? item.description : item.encoded;
    var image = element.querySelector('img') || {}
    return image.src || ''
  }

  /**
   * Truncate a text and concatenate with ellipsis if needed.
   * @param {String} text Text to truncate.
   * @param {Number} maxLength Max length of the text.
   * @return {String} Truncated text.
   */
  _truncateText(text, maxLength) {
    return maxLength && text.length > maxLength
      ? text.substr(0, maxLength) + '...'
      : text
  }
}

customElements.define(RssItems.is, RssItems);
