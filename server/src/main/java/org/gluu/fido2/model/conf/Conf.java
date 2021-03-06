/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.fido2.model.conf;

import org.gluu.oxauth.model.config.StaticConfiguration;
import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DN;
import org.gluu.persist.annotation.DataEntry;
import org.gluu.persist.annotation.JsonObject;
import org.gluu.persist.annotation.ObjectClass;

/**
 * @author Yuriy MOvchan
 * @version May 12, 2020
 */
@DataEntry
@ObjectClass(value = "gluuApplicationConfiguration")
public class Conf {
    @DN
    private String dn;

    @JsonObject
    @AttributeName(name = "gluuConfDynamic")
    private AppConfiguration dynamicConf;

    @JsonObject
    @AttributeName(name = "gluuConfStatic")
    private StaticConfiguration staticConf;

    @AttributeName(name = "oxRevision")
    private long revision;

    public Conf() {
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String p_dn) {
        dn = p_dn;
    }

	public AppConfiguration getDynamicConf() {
		return dynamicConf;
	}

	public void setDynamicConf(AppConfiguration dynamicConf) {
		this.dynamicConf = dynamicConf;
	}

	public StaticConfiguration getStaticConf() {
		return staticConf;
	}

	public void setStaticConf(StaticConfiguration staticConf) {
		this.staticConf = staticConf;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	@Override
	public String toString() {
		return "Conf [dn=" + dn + ", dynamicConf=" + dynamicConf + ", staticConf=" + staticConf + ", revision=" + revision + "]";
	}
}
