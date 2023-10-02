package dev.anonymous.social.media.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.anonymous.social.media.R
import dev.anonymous.social.media.utils.Constants
import javax.inject.Singleton

@Module
// will live as long as the <?> dose)
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGlide(
        @ApplicationContext context: Context,
    ): RequestManager = Glide.with(context)
        .applyDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(R.color.place_holder_color)
                .placeholder(R.drawable.place_holder_color)
        )

    @Singleton
    @Provides
    fun provideDrawableTransitionOptions(): DrawableTransitionOptions =
        DrawableTransitionOptions.withCrossFade(Constants.TRANSITION_CROSS_FADE_DURATION)
}