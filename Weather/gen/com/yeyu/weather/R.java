/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package com.yeyu.weather;

public final class R {
    public static final class attr {
        /**  Background color for CardView. 
         <p>Must be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int cardBackgroundColor=0x7f010000;
        /**  Corner radius for CardView. 
         <p>Must be a dimension value, which is a floating point number appended with a unit such as "<code>14.5sp</code>".
Available units are: px (pixels), dp (density-independent pixels), sp (scaled pixels based on preferred font size),
in (inches), mm (millimeters).
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int cardCornerRadius=0x7f010001;
    }
    public static final class color {
        public static final int blue=0x7f060004;
        public static final int cardview_dark_background=0x7f060000;
        public static final int cardview_light_background=0x7f060001;
        public static final int cardview_shadow_end_color=0x7f060002;
        public static final int cardview_shadow_start_color=0x7f060003;
    }
    public static final class dimen {
        public static final int cardview_default_radius=0x7f070000;
        public static final int cardview_elevation=0x7f070001;
        public static final int cardview_shadow_size=0x7f070002;
    }
    public static final class drawable {
        public static final int cloudy=0x7f020000;
        public static final int cloudy_fog=0x7f020001;
        public static final int fog=0x7f020002;
        public static final int forcast_view_background=0x7f020003;
        public static final int ic_launcher=0x7f020004;
        public static final int moon=0x7f020005;
        public static final int partlycloudy=0x7f020006;
        public static final int partlycloudynight=0x7f020007;
        public static final int rainy_h=0x7f020008;
        public static final int rainy_m=0x7f020009;
        public static final int rainy_s=0x7f02000a;
        public static final int rainy_snowy=0x7f02000b;
        public static final int snowy_h=0x7f02000c;
        public static final int snowy_s=0x7f02000d;
        public static final int sunny=0x7f02000e;
        public static final int thunder=0x7f02000f;
        public static final int thunder_rainy=0x7f020010;
        public static final int thunder_rainy_h=0x7f020011;
        public static final int thunder_rainy_m=0x7f020012;
        public static final int windy=0x7f020013;
    }
    public static final class id {
        public static final int activity_main=0x7f080000;
        public static final int cardview_1=0x7f080005;
        public static final int cardview_2=0x7f080006;
        public static final int cardview_3=0x7f080007;
        public static final int cardview_4=0x7f080008;
        public static final int cardview_5=0x7f080009;
        public static final int cardview_6=0x7f08000a;
        public static final int cardview_7=0x7f08000b;
        public static final int climate_celsius=0x7f080002;
        public static final int climate_icon=0x7f080001;
        public static final int forcast_celsius=0x7f080004;
        public static final int more=0x7f080003;
    }
    public static final class layout {
        public static final int activity_main=0x7f030000;
        public static final int card_view=0x7f030001;
        public static final int forcast_merge_layout=0x7f030002;
        public static final int fragment_main=0x7f030003;
    }
    public static final class string {
        public static final int app_name=0x7f040000;
    }
    public static final class style {
        /** 
        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 

        Base application theme, dependent on API level. This theme is replaced
        by AppBaseTheme from res/values-vXX/styles.xml on newer devices.
    

            Theme customizations available in newer API levels can go in
            res/values-vXX/styles.xml, while customizations related to
            backward-compatibility can go here.
        

        Base application theme for API 11+. This theme completely replaces
        AppBaseTheme from res/values/styles.xml on API 11+ devices.
    
 API 11 theme customizations can go here. 

        Base application theme for API 14+. This theme completely replaces
        AppBaseTheme from BOTH res/values/styles.xml and
        res/values-v11/styles.xml on API 14+ devices.
    
 API 14 theme customizations can go here. 
         */
        public static final int AppBaseTheme=0x7f050000;
        /**  Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
 Application theme. 
 All customizations that are NOT specific to a particular API-level can go here. 
         */
        public static final int AppTheme=0x7f050001;
        public static final int CardView=0x7f050002;
        public static final int CardView_Dark=0x7f050003;
        public static final int CardView_Light=0x7f050004;
    }
    public static final class styleable {
        /** Attributes that can be used with a CardView.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #CardView_cardBackgroundColor com.yeyu.weather:cardBackgroundColor}</code></td><td> Background color for CardView.</td></tr>
           <tr><td><code>{@link #CardView_cardCornerRadius com.yeyu.weather:cardCornerRadius}</code></td><td> Corner radius for CardView.</td></tr>
           </table>
           @see #CardView_cardBackgroundColor
           @see #CardView_cardCornerRadius
         */
        public static final int[] CardView = {
            0x7f010000, 0x7f010001
        };
        /**
          <p>
          @attr description
           Background color for CardView. 


          <p>Must be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          <p>This is a private symbol.
          @attr name com.yeyu.weather:cardBackgroundColor
        */
        public static final int CardView_cardBackgroundColor = 0;
        /**
          <p>
          @attr description
           Corner radius for CardView. 


          <p>Must be a dimension value, which is a floating point number appended with a unit such as "<code>14.5sp</code>".
Available units are: px (pixels), dp (density-independent pixels), sp (scaled pixels based on preferred font size),
in (inches), mm (millimeters).
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          <p>This is a private symbol.
          @attr name com.yeyu.weather:cardCornerRadius
        */
        public static final int CardView_cardCornerRadius = 1;
    };
}
