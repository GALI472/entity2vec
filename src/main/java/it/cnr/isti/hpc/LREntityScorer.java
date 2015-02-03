package it.cnr.isti.hpc;


public class LREntityScorer extends EntityScorer {

	public LREntityScorer(Word2VecCompress word_model,
			Word2VecCompress entity_model) {
		super(word_model, entity_model);
		if (entity_model.dimensions() != word_model.dimensions() + 1) {
			throw new IllegalArgumentException(
					"Word and entity models have incompatible vector dimensions");
		}
	}

	public class LRScorerContext extends ScorerContext {
		public LRScorerContext(float[] word_vecs, int[] word_counts) {
			super(word_vecs, word_counts);
		}

		@Override
		float compute_score() {
			int n_words = word_counts.length;
			int word_size = word_model.dimensions();
			float s = 0;
			for (int i = 0; i < n_words; ++i) {
				int word_count = word_counts[i];
				int word_offset = i * word_size;
				double dotprod = entity_vec[word_size];
				dotprod += LinearAlgebra.inner(word_size, word_vecs,
						word_offset, entity_vec, 0);
				s += word_count * Math.log(1 + Math.exp(dotprod));
			}

			return -s;
		}
	}

	@Override
	ScorerContext create_context(float[] word_vecs, int[] word_counts) {
		return new LRScorerContext(word_vecs, word_counts);
	}

}
