/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.validation;

import java.io.Serializable;


/**
 * <p>
 * <small>
 * BaseValidator MessageSpecification.
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public class ValidatorMessage implements Serializable, ValidatorMessageHolder {
    private String detail;
    private String summary;
    private boolean hasError = false;


    public ValidatorMessage( String summary, String detail ) {
        this.summary = summary;
        this.detail = detail;
        hasError = true;
    }

    public ValidatorMessage( String message ) {
        this.summary = message;
        this.detail = message;
        hasError = true;
    }

    public ValidatorMessage() {
        this.summary = "Message not setup!";
        this.detail = "Message not setup!";
        hasError = false;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail( String detail ) {
        this.detail = detail;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary( String summary ) {
        this.summary = summary;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public void setHasError( boolean aHasError ) {
        this.hasError = aHasError;
    }


    @Override
    public String toString() {
        return "ValidatorMessage{" +
                "detail='" + detail + '\'' +
                ", summary='" + summary + '\'' +
                ", hasError=" + hasError +
                '}';
    }
}
