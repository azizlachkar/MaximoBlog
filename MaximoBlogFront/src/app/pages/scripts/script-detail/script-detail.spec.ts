import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScriptDetail } from './script-detail';

describe('ScriptDetail', () => {
  let component: ScriptDetail;
  let fixture: ComponentFixture<ScriptDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScriptDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScriptDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
