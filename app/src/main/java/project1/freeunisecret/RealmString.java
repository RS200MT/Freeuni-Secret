package project1.freeunisecret;

import io.realm.RealmObject;

/**
 * Created by Tornike on 30-Jul-17.
 */

public class RealmString extends RealmObject {
	private String str;

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public RealmString(){

		}

}
