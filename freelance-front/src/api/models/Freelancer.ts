/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Competence } from './Competence';
export type Freelancer = {
    id?: number;
    nom?: string;
    prenom?: string;
    email?: string;
    experience?: number;
    age?: number;
    gender?: Freelancer.gender;
    profil?: string;
    competences?: Array<Competence>;
};
export namespace Freelancer {
    export enum gender {
        HOMME = 'HOMME',
        FEMME = 'FEMME',
    }
}

