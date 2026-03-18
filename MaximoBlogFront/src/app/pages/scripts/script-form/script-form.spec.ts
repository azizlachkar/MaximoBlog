import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScriptForm } from './script-form';

describe('ScriptForm', () => {
  let component: ScriptForm;
  let fixture: ComponentFixture<ScriptForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScriptForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScriptForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
