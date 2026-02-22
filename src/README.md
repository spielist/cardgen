# "CardGen" - Playing Card Generator

This playing card generator can generate any number of playing cards from a supplied definition file, allowing the user
to define aspects like the canvas and card size, output image format, and layers of images and text that should be 
combined to create each card. Definitions can be commented out by prefixing them with the "#" symbol.

## The "CardGen" file format

The file format supports several different directives, which can be placed in a text document in any order.

### `CANVAS` directive

The `CANVAS` directive specifies the overall dimensions of the images to be produced, and the output image file format.
Only one `CANVAS` directive is required.
```
CANVAS=820|1120|png
```

### `CARDDEF` directive

The `CARDDEF` directive specifies the overall size and positioning of the card portion of the image
Only one `CANVAS` directive is required.

```
CARDDEF=820|1120|1|1
```

### `FONTDEF` directive

The `FONTDEF` directive provides font specifications, such as the font file, font size, font style, fore color, and background color.
The first token of the directive should be a unique name for the font specification so that it can be referred to by other directives.
Multiple `FONTDEF` directives can be added, in any order.

```
FONTDEF=TITLE_FONT|assets/1785GLCBaskervilleNormal.otf|96|bold|#220A0B|none
FONTDEF=SUBTITLE_FONT|assets/1785GLCBaskervilleNormal.otf|72|bold|#220A0B|none
FONTDEF=FOOTER_FONT|assets/1785GLCBaskervilleNormal.otf|58|plain|#331313|none
FONTDEF=CARDNUM_FONT|assets/1785GLCBaskervilleNormal.otf|28|italic|#331313|none
FONTDEF=LEVEL_FONT|assets/1785GLCBaskervilleNormal.otf|58|bold|#331313|none
```

### `FILEDEF` directive

The `FILEDEF` directive provides for a filename and layer visibility. The first token of the directive should be a unique name for
the file so it can be referred to the card definitions (below). Multiple `FILEDEF` directives can be added, in any order.

```
FILEDEF=REDBACK|assets/color-red.png|true
FILEDEF=GRNBACK|assets/color-green.png|true
FILEDEF=YELBACK|assets/color-yellow.png|true
FILEDEF=BLUBACK|assets/color-blue.png|true
FILEDEF=GUIDELINES|assets/guidelines.png|false
FILEDEF=TRIMCARD|assets/trimcard.png|true
```

### `TEXTDEF` directive

The `TEXTDEF` directive provides for text to be rendered, the `FONTDEF` to be used to render the text, and absolute or relative
positioning of the text. The first token of the directive should be a unique name for the text, so it can be referenced in the card
definitions (below). Multiple `TEXTDEF` directives can be added, in any order. If the text is "#", the card number is output instead.

```
TEXTDEF=MONK|Monk|TITLE_FONT|center|140
TEXTDEF=MONKSUB|A holy man|SUBTITLE_FONT|center|140
TEXTDEF=MONKFOOT|Devoted to meditation and prayer|FOOTER_FONT|center|bottom-120
TEXTDEF=HOBBIT|Hobbit|TITLE_FONT|center|140
TEXTDEF=HOBBITSUB|Congenial|SUBTITLE_FONT|center|140
TEXTDEF=HOBBITFOOT|Devoted to friends and family|FOOTER_FONT|center|bottom-120
TEXTDEF=CARDNUM|#|CARDNUM_FONT|right-85|bottom-60
```

### `TEXT()` directive

The `TEXT()` directive allows for dynamic text to be rendered on the card, with the ability to specify the text, `FONTDEF`,
and absolute or relative positioning of the text. `TEXT()` directives are only used within `CARD` directives (see below),
but multiple `TEXT()` directives can be used to render separate lines of text.

### `FILE()` directive

The `FILE()` directive allows for a file/graphics layer to be rendered on the card, with the ability to specify the file name.
`FILE()` directives are only used within `CARD` directives (see below), but multiple `FILE()` directives can be used to render
separate file layers on the card.

### `CARD` directive

The `CARD` directive specifies the layers to be combined to generate the final card. The first token of the `CARD` directive 
specifies the relative path and name of the output card file (without file extension). The remaining tokens define the layers 
to be combined to generate the final card and may be one of the following: `FILEDEF`, `TEXTDEF`, `TEXT()`, or `FILE()`

```
CARD=output/front/001f,REDBACK,#,MONK,MONKSUB,MONKFOOT,FILE(assets/monkpose01.png),TEXT(17|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/002f,GRNBACK,#,MONK,MONKSUB,MONKFOOT,FILE(assets/monkpose02.png),TEXT(18|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/003f,YELBACK,#,MONK,MONKSUB,MONKFOOT,FILE(assets/monkpose03.png),TEXT(19|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/004f,BLUBACK,#,MONK,MONKSUB,MONKFOOT,FILE(assets/monkpose04.png),TEXT(20|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/005f,REDBACK,#,HOBBIT,HOBBITSUB,HOBBITFOOT,FILE(assets/hobbitpose01.png),TEXT(13|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/006f,GRNBACK,#,HOBBIT,HOBBITSUB,HOBBITFOOT,FILE(assets/hobbitpose02.png),TEXT(14|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/007f,YELBACK,#,HOBBIT,HOBBITSUB,HOBBITFOOT,FILE(assets/hobbitpose03.png),TEXT(15|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
CARD=output/front/008f,BLUBACK,#,HOBBIT,HOBBITSUB,HOBBITFOOT,FILE(assets/hobbitpose04.png),TEXT(16|LEVEL_FONT|right-105|136),TRIMCARD,GUIDELINES
```
