package com.heliumhq.api_impl;

import com.heliumhq.util.geom.Rectangle;

public enum SearchDirection {

	TO_LEFT_OF {
		@Override
		public boolean isInDirection(Rectangle r1, Rectangle r2) {
			return r1.isToLeftOf(r2);
		}
	},
	TO_RIGHT_OF {
		@Override
		public boolean isInDirection(Rectangle r1, Rectangle r2) {
			return r1.isToRightOf(r2);
		}
	},
	ABOVE {
		@Override
		public boolean isInDirection(Rectangle r1, Rectangle r2) {
			return r1.isAbove(r2);
		}
	},
	BELOW {
		@Override
		public boolean isInDirection(Rectangle r1, Rectangle r2) {
			return r1.isBelow(r2);
		}
	};

	public abstract boolean isInDirection(Rectangle r1, Rectangle r2);

}
