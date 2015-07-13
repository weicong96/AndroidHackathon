/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-06-30 18:20:40 UTC)
 * on 2015-07-13 at 03:09:07 UTC 
 * Modify at your own risk.
 */

package com.example.admin.myapplication.backend.userApi.model;

/**
 * Model definition for Achievements.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the userApi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Achievements extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long achievementID;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String achievementTitle;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getAchievementID() {
    return achievementID;
  }

  /**
   * @param achievementID achievementID or {@code null} for none
   */
  public Achievements setAchievementID(java.lang.Long achievementID) {
    this.achievementID = achievementID;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAchievementTitle() {
    return achievementTitle;
  }

  /**
   * @param achievementTitle achievementTitle or {@code null} for none
   */
  public Achievements setAchievementTitle(java.lang.String achievementTitle) {
    this.achievementTitle = achievementTitle;
    return this;
  }

  @Override
  public Achievements set(String fieldName, Object value) {
    return (Achievements) super.set(fieldName, value);
  }

  @Override
  public Achievements clone() {
    return (Achievements) super.clone();
  }

}
