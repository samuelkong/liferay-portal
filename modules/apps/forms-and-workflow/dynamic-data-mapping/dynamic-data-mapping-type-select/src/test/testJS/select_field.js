'use strict';

var A = AUI();

describe(
	'Liferay.DDM.Field.Select',
	function() {
		var selectField;

		afterEach(
			function(done) {
				selectField.destroy();

				done();
			}
		);

		before(
			function(done) {
				AUI().use(
					'liferay-ddm-form-field-select-template',
					'liferay-ddm-form-field-select',
					function(A) {
						Liferay.DDM.Renderer.FieldTypes.register(
							{
								'javaScriptClass': 'Liferay.DDM.Field.Select',
								'name': 'select',
								'templateNamespace': 'ddm.select'
							}
						);

						done();
					}
				);
			}
		);

		beforeEach(
			function(done) {
				selectField = new Liferay.DDM.Field.Select(
					{
						context: {
							name: 'selectField',
							required: true
						}
					}
				).render(document.body);

				done();
			}
		);

		describe(
			'.getValue()',
			function() {
				it(
					'should return string value for single selectField',
					function(done) {
						selectField.set('multiple', false);

						selectField.set('options', [{label: 'a', value: 'a'}]);

						selectField.setValue(['a']);

						assert.equal(
							selectField.getValue(),
							'a'
						);

						done();
					}
				);
			}
		);

		describe(
			'.clickSelectTrigger()',
			function() {
				it(
					'should add the focus class when opened',
					function(done) {
						var container = selectField.get('container');

						var divSelect = container.one('.form-builder-select-field');

						assert.isFalse(divSelect.hasClass('active'));

						divSelect.simulate('mousedown');

						assert.isTrue(divSelect.hasClass('active'));

						done();
					}
				);
			}
		);

		describe(
			'.closeList()',
			function() {
				it(
					'should close the list after click in document',
					function(done) {
						var container = selectField.get('container');

						var divSelect = container.one('.form-builder-select-field');

						divSelect.simulate('mousedown');

						assert.isNull(container.one('.drop-chosen.hide'));

						A.one(document).simulate('click');

						assert.isNotNull(container.one('.drop-chosen.hide'));

						done();
					}
				);

				it(
					'should remove the focus class when closed',
					function(done) {
						var container = selectField.get('container');

						var divSelect = container.one('.form-builder-select-field');

						divSelect.simulate('mousedown');

						assert.isTrue(divSelect.hasClass('active'));

						A.one(document).simulate('click');

						assert.isFalse(divSelect.hasClass('active'));

						done();
					}
				);
			}
		);

		describe(
			'.cleanSelect()',
			function() {
				it(
					'should clean value of the select',
					function() {
						selectField.set('options', [{label: 'a', value: 'a'}]);

						selectField.setValue(['a']);

						selectField.cleanSelect();

						assert.equal(
							selectField.get('value').length,
							0
						);
					}
				);
			}
		);

		describe(
			'.clickItem()',
			function() {
				it(
					'should click item and get it value',
					function() {
						selectField.set('options', [{label: 'foo', value: 'foo'}, {label: 'bar', value: 'bar'}]);

						var container = selectField.get('container');

						container.one('.form-builder-select-field').simulate('mousedown');

						var item = container.one('.form-builder-select-field').one('.drop-chosen ul li');

						item.simulate('mousedown');

						assert.equal(
							selectField.getValue(),
							'foo'
						);
					}
				);
			}
		);
	}
);