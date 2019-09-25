# ExactRatingBar


#### RatingBar with exact values. </br>
<img src="https://github.com/Jamshid-M/ExactRatingBar/blob/master/sources/example.gif" height="550">

<img src="https://github.com/Jamshid-M/ExactRatingBar/blob/master/sources/7_stars.png" height="408" width="243"> <img src="https://github.com/Jamshid-M/ExactRatingBar/blob/master/sources/size_32.png" height="408" width="243"> <img src="https://github.com/Jamshid-M/ExactRatingBar/blob/master/sources/size_96.png" height="408" width="243">
## Usage

Add it in your root build.gradle at the end of repositories
```
repositories {

        maven { url 'https://jitpack.io' }
    }
```

```
dependencies {
	implementation 'com.github.Jamshid-M:ExactRatingBar:1.0.0'
}
```

Open activity and specify ExactRatingBar object and setup listener for it, you can use simple interface callback or lambda

```
        var rate = findViewById<ExactRatingBar>(R.id.rate)
        rate.onRatingBarChanged = object : ExactRatingBar.OnRatingBarChanged{
            override fun newRate(rate: Float) {
                Toast.makeText(this@MainActivity, rate.toString(), Toast.LENGTH_LONG).show()
                Log.d("MYTAG", "interface$rate")
            }
        }

        rate.onRateChanged = {
            Log.d("MYTAG", "lambda$it")
        }
```


You can set properties of RatingBar in xml
```
<uz.jamshid.library.ExactRatingBar
        android:id="@+id/rate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        app:starCount="5"
        app:isIndicator="false"
        app:starFillColor="@color/colorAccent"
        app:starSize="96"
        app:starValue="3.5"
        />
```

```starCount``` specifies number of stars </br>
```isIndicator``` enables/disables touch listener, by which user enables or disables seeking in RatingBar</br>
```starFillColor``` color of star</br>
```starSize``` size of star</br>
```starValue``` rate of star, e.g 3.5 out of 5</br>
