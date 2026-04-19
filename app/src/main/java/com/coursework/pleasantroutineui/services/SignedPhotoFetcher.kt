package com.coursework.pleasantroutineui.services

import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.ImageRequest
import coil.request.Options
import coil.request.SuccessResult
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.provider.SignedLinkProvider

//class SignedPhotoFetcher(
//    private val data: LinkDto,
//    private val provider: SignedLinkProvider,
//    private val imageLoader: ImageLoader
//) : Fetcher {
//
//    override suspend fun fetch(): FetchResult {
//
//        val signedUrl = provider.getSignedLink(data.link)
//
//        val request = ImageRequest.Builder(imageLoader.defaults.context)
//            .data(signedUrl)
//            .build()
//
//        val result = imageLoader.execute(request)
//
//        val drawable = (result as? SuccessResult)?.drawable
//            ?: error("Unable load image")
//
//        return DrawableResult(
//            drawable = drawable,
//            isSampled = false,
//            dataSource = DataSource.NETWORK
//        )
//    }
//
//    class Factory(
//        private val provider: SignedLinkProvider
//    ) : Fetcher.Factory<LinkDto> {
//
//        override fun create(
//            data: LinkDto,
//            options: Options,
//            imageLoader: ImageLoader
//        ): Fetcher {
//            return SignedPhotoFetcher(data, provider, imageLoader)
//        }
//    }
//}
