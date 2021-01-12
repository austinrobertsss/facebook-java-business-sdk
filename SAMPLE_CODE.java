/**
 * Copyright (c) 2015-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

import com.facebook.ads.sdk.*;
import java.io.File;
import java.util.Arrays;

public class SAMPLE_CODE {
  public static void main (String args[]) throws APIException {

    String access_token = "EAAJdQrZA6fdgBADZCvDBWD7wvBxg9OzhKeo1sabyf5jstzMyVmF0PFpKep2NUEgRlnb5qfizFmKGYHMBEl6FuSfd4Ogx1jZBINIBup6PThPLJGtoXThriUUp000WCiPjDEzmhPH8puZB2cdSwmDc2rxoDaBiojqpZB77F9EjCsblakoGyv8qmXmxZAiHsHOeQZD";
    String app_secret = "ff35f8b4d2352138ab4508f85a0dcef5";
    String ad_account_id = "412496270105828";
    String business_id = "181977202654788";
    String page_id = "125854425637776";
    String pixel_id = "408440653810180";
    String app_id = "665491064126936";
    APIContext context = new APIContext(access_token).enableDebug(true);

    ProductCatalog productCatalog = new Business(business_id, context).createOwnedProductCatalog()
      .setName("Test Catalog")
      .execute();
    String product_catalog_id = productCatalog.getId();
    new ProductCatalog(product_catalog_id, context).createProductFeed()
      .setName("Test Feed")
      .setSchedule("{\"interval\":\"DAILY\",\"url\":\"https://developers.facebook.com/resources/dpa_product_catalog_sample_feed.csv\",\"hour\":\"22\"}")
      .execute();
    ProductSet productSet = new ProductCatalog(product_catalog_id, context).createProductSet()
      .setName("All Product")
      .execute();
    String product_set_id = productSet.getId();
    new ProductCatalog(product_catalog_id, context).createExternalEventSource()
      .setExternalEventSources("[\"" + pixel_id + "\"]")
      .execute();
    Campaign campaign = new AdAccount(ad_account_id, context).createCampaign()
      .setName("My Campaign")
      .setObjective(Campaign.EnumObjective.VALUE_PRODUCT_CATALOG_SALES)
      .setPromotedObject("{\"product_catalog_id\":\"" + product_catalog_id + "\"}")
      .setStatus(Campaign.EnumStatus.VALUE_PAUSED)
      .execute();
    String campaign_id = campaign.getId();
    AdSet adSet = new AdAccount(ad_account_id, context).createAdSet()
      .setName("My AdSet")
      .setOptimizationGoal(AdSet.EnumOptimizationGoal.VALUE_OFFSITE_CONVERSIONS)
      .setBillingEvent(AdSet.EnumBillingEvent.VALUE_IMPRESSIONS)
      .setBidAmount(20L)
      .setPromotedObject("{\"product_set_id\": " + product_set_id + "}")
      .setDailyBudget(1000L)
      .setCampaignId(campaign_id)
      .setTargeting(
          new Targeting()
            .setFieldGeoLocations(
              new TargetingGeoLocation()
                .setFieldCountries(Arrays.asList("US"))
            )
        )
      .setStatus(AdSet.EnumStatus.VALUE_PAUSED)
      .execute();
    String ad_set_id = adSet.getId();
    AdCreative creative = new AdAccount(ad_account_id, context).createAdCreative()
      .setName("My Creative")
      .setObjectStorySpec(
          new AdCreativeObjectStorySpec()
            .setFieldPageId(page_id)
            .setFieldTemplateData(
              new AdCreativeLinkData()
                .setFieldCallToAction(
                  new AdCreativeLinkDataCallToAction()
                    .setFieldType(AdCreativeLinkDataCallToAction.EnumType.VALUE_SHOP_NOW)
                )
                .setFieldDescription("{{product.description}}")
                .setFieldLink("www.example.com")
                .setFieldMessage("{{product.name | titleize}}")
                .setFieldName("{{product.name}} - {{product.price}}")
            )
        )
      .setApplinkTreatment(AdCreative.EnumApplinkTreatment.VALUE_WEB_ONLY)
      .setProductSetId(product_set_id)
      .setUrlTags("utm_source=facebook")
      .execute();
    String creative_id = creative.getId();
    Ad ad = new AdAccount(ad_account_id, context).createAd()
      .setName("My Ad")
      .setAdsetId(ad_set_id)
      .setCreative(
          new AdCreative()
            .setFieldId(creative_id)
        )
      .setTrackingSpecs("[ {\"action_type\": [\"offsite_conversion\"], \"fb_pixel\": [\"" + pixel_id + "\"]} ]")
      .setStatus(Ad.EnumStatus.VALUE_PAUSED)
      .execute();
    String ad_id = ad.getId();
    new Ad(ad_id, context).getPreviews()
      .setAdFormat(AdPreview.EnumAdFormat.VALUE_DESKTOP_FEED_STANDARD)
      .execute();
  }
}
