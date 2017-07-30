package project1.freeunisecret;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Tornike on 30-Jul-17.
 */

public class Heart extends RealmObject {
		private String postId;
		private RealmList<RealmString> lovers;

		public Heart(){

		}

		public Heart(String postId, RealmList<RealmString> lovers){
			this.postId = postId;
			this.lovers = lovers;

		}

		public String getPostId() {
			return postId;
		}

		public void setPostId(String postId) {
			this.postId = postId;
		}

		public RealmList<RealmString> getLovers() {
			return lovers;
		}

		public void setLovers(RealmList<RealmString> lovers) {
			this.lovers = lovers;
		}
} 
